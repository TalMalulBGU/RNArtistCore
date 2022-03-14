
import tempfile
import subprocess
from flask import Flask, Response
from obj_def import KTSData, KTSString
from Files.KTS import KTS
from pydantic_webargs import webargs
from flask import send_file


app = Flask(__name__)

KTS_INPUT_FILE_NAME = 'input'
KTS_OUT_FILE_NAME = 'rna'
KTS_INPUT_FILE_EXTENSION = 'kts'
RNARTSITCORE_EXECUTION_PATH = 'rnartistcore.jar'


@app.route('/exec-rnartsitcore-template', methods = ['POST'])
@webargs(body=KTSData)
def exec_rnartsitcore_template(**kwargs):

    body = KTSData(**kwargs["payload"])
    body = body.dict()

    molecule = body['molecule']
    brackets = body['brackets']
    format = body['format']

    molecule = molecule.upper().replace('T','U')

    temp_dir_location = tempfile.mkdtemp(dir='resources/rnartis_files/')
    input_kts_file_path = '{}/{}.{}'.format(temp_dir_location, KTS_INPUT_FILE_NAME, KTS_INPUT_FILE_EXTENSION)
    kts = KTS(molecule=molecule, brackets=brackets, file_location=temp_dir_location, filename=KTS_OUT_FILE_NAME, format=format)
    kts.save(input_kts_file_path)

    java = subprocess.run(["java", "-jar", RNARTSITCORE_EXECUTION_PATH, input_kts_file_path])

    core_out_path = '{}/{}.{}'.format(temp_dir_location, KTS_OUT_FILE_NAME, format)


    if java.returncode == 0:
        if format == 'svg':
            return send_file(core_out_path, mimetype='image/svg+xml')
        elif format == 'png':
            return send_file(core_out_path, mimetype='image/png')
        else:
            return Response("{'input error':'format {} is not supported.\nplease use svg or pdf'}".format(format), status=400, mimetype='application/json')
    else:
        return java.stderr

@app.route('/exec-rnartsitcore-string', methods = ['POST'])
@webargs(body=KTSString)
def exec_rnartsitcore_string(**kwargs):

    body = KTSString(**kwargs["payload"])
    body = body.dict()

    kts_content = body['kts_content']
    format = body['format']


    temp_dir_location = tempfile.mkdtemp(dir='resources/rnartis_files/')
    input_kts_file_path = '{}/{}.{}'.format(temp_dir_location, KTS_INPUT_FILE_NAME, KTS_INPUT_FILE_EXTENSION)

    with open(input_kts_file_path) as kts_file:
        kts_content.replace('###OUT_FILE_LOCATION###', temp_dir_location)
        kts_content.replace('###OUT_FILENAME###', temp_dir_location)
        kts_content.replace('###OUT_FILE_FORMAT###', format)
        kts_file.write(kts_content)
        kts_file.flush()

    java = subprocess.run(["java", "-jar", RNARTSITCORE_EXECUTION_PATH, input_kts_file_path])

    core_out_path = '{}/{}.{}'.format(temp_dir_location, KTS_OUT_FILE_NAME, format)

    if java.returncode == 0:
        if format == 'svg':
            return send_file(core_out_path, mimetype='image/svg+xml')
        elif format == 'pdf':
            return send_file(core_out_path, mimetype='image/png')
        else:
            return Response("{'input error':'format {} is not supported.\nplease use svg or pdf'}", status=400, mimetype='application/json')
    else:
        return java.stderr

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
    # app.run(debug=True)