import json


class KTS():

    def __init__(self, molecule, brackets, file_location, filename = 'out', format = 'svg'):
        """
        param molecule : GuideRNA + scaffold (string)
        param brackets : bracket matching the mol (string)
        """
        self._molecule = molecule
        self._brackets = brackets
        self._filename = filename
        self._file_location = file_location
        self._format = format

    def save(self, filepath):
        kts_str = ''
        with open('resources/kts_templates/kts_template.kts', 'r') as file :
            kts_template = file.read()
            kts_template = kts_template.replace('###MOLECULE###', self._molecule)
            kts_template = kts_template.replace('###BRACKETS###', self._brackets)
            kts_template = kts_template.replace('###OUT_FILENAME###', self._filename)
            kts_template = kts_template.replace('###OUT_FILE_FORMAT###', self._format)
            kts_template = kts_template.replace('###OUT_FILE_LOCATION###', self._file_location)
            kts_str = kts_template

        with open(filepath, 'w') as file :
            file.write(kts_str)
            file.flush()

    @property
    def molecule(self):
        return self._molecule

    @molecule.setter
    def molecule(self, value):
        self._molecule = value

    @molecule.deleter
    def molecule(self):
        del self._molecule

    @property
    def brackets(self):
        return self._brackets

    @brackets.setter
    def brackets(self, value):
        self._brackets = value

    @brackets.deleter
    def brackets(self):
        del self._brackets

    def __str__(self):
        kts_str = ''
        with open('..resources/kts_template.kts', 'r') as file :
            kts_template = file.read()
            kts_template = kts_template.replace('###MOLECULE###', self._molecule)
            kts_template = kts_template.replace('###BRACKETS###', self._brackets)
            kts_str = kts_template

        return kts_str