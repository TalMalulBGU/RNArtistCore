package fr.unistra.rnartist.model

import com.google.gson.Gson
import fr.unistra.rnartist.model.io.getUserDir
import org.apache.commons.lang3.tuple.MutablePair
import org.jdom.Document
import org.jdom.Element
import org.jdom.JDOMException
import org.jdom.input.SAXBuilder
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import java.awt.Color
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.prefs.BackingStoreException

object RnartistConfig {

    @JvmField
    val defaultColorSchemes: Map<String, Map<String, String>> = mapOf(
            "Candies" to mapOf(
                    ThemeParameter.AColor.toString() to getHTMLColorString(Color(0, 192, 255)),
                    ThemeParameter.UColor.toString() to getHTMLColorString(Color(192, 128, 128)),
                    ThemeParameter.GColor.toString() to getHTMLColorString(Color(128, 192, 0)),
                    ThemeParameter.CColor.toString() to getHTMLColorString(Color(255, 0, 255)),
                    ThemeParameter.XColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.SecondaryColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.TertiaryColor.toString() to getHTMLColorString(Color(255, 192, 128))
            ),

            "Grapes" to mapOf(
                    ThemeParameter.AColor.toString() to getHTMLColorString(Color(128, 128, 0)),
                    ThemeParameter.UColor.toString() to getHTMLColorString(Color(128, 128, 128)),
                    ThemeParameter.GColor.toString() to getHTMLColorString(Color(192, 0, 0)),
                    ThemeParameter.CColor.toString() to getHTMLColorString(Color(255, 128, 0)),
                    ThemeParameter.XColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.SecondaryColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.TertiaryColor.toString() to getHTMLColorString(Color(255, 192, 128))
            ),

            "Metal" to mapOf(
                    ThemeParameter.AColor.toString() to getHTMLColorString(Color(153, 77, 0)),
                    ThemeParameter.UColor.toString() to getHTMLColorString(Color(179, 128, 26)),
                    ThemeParameter.GColor.toString() to getHTMLColorString(Color(179, 179, 179)),
                    ThemeParameter.CColor.toString() to getHTMLColorString(Color(77, 77, 77)),
                    ThemeParameter.XColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.SecondaryColor.toString() to getHTMLColorString(Color.LIGHT_GRAY),
                    ThemeParameter.TertiaryColor.toString() to getHTMLColorString(Color(153, 153, 51))
            )
    )

    private var document: Document? = null
    @JvmField
    var defaultTheme = mutableMapOf<String,String>(
            ThemeParameter.AColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.AColor.toString())!!,
            ThemeParameter.UColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.UColor.toString())!!,
            ThemeParameter.GColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.GColor.toString())!!,
            ThemeParameter.CColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.CColor.toString())!!,
            ThemeParameter.XColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.XColor.toString())!!,
            ThemeParameter.SecondaryColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.SecondaryColor.toString())!!,
            ThemeParameter.TertiaryColor.toString() to defaultColorSchemes.get("Candies")!!.get(ThemeParameter.TertiaryColor.toString())!!,
            ThemeParameter.ResidueBorder.toString() to "2",
            ThemeParameter.SecondaryInteractionWidth.toString() to "4",
            ThemeParameter.TertiaryInteractionWidth.toString() to "2",
            ThemeParameter.HaloWidth.toString() to "10",
            ThemeParameter.TertiaryOpacity.toString() to "50",
            ThemeParameter.TertiaryInteractionStyle.toString() to "dashed",
            ThemeParameter.FontName.toString() to "Arial",
            ThemeParameter.DeltaXRes.toString() to "0",
            ThemeParameter.DeltaYRes.toString() to "0",
            ThemeParameter.DeltaFontSize.toString() to "0"
    )

    @JvmStatic
    @Throws(BackingStoreException::class, IOException::class)
    fun loadConfig() {
        if (document != null) return
        val configFile = File(getUserDir(), "config.xml")
        if (configFile.exists()) {
            val builder = SAXBuilder()
            try {
                document = builder.build(configFile)
                val drawing = document!!.getRootElement().getChild("theme")
                if (drawing != null) {
                    for (c in drawing.getChildren()) {
                        defaultTheme[(c as Element).name] =  c.text
                    }
                }
            } catch (e: JDOMException) {
                e.printStackTrace()
            }
        } else {
            val root = Element("rnartist-config")
            root.setAttribute("release", getRnartistRelease())
            document = Document(root)
        }
        recoverWebsite()
    }

    @JvmStatic
    @Throws(IOException::class)
    fun saveConfig(theme:Theme) {
        var drawing = document!!.rootElement.getChild("theme")
        if (drawing == null) {
            drawing = Element("theme")
            document!!.rootElement.addContent(drawing)
        } else
            drawing.removeContent()
        for ((k, v) in theme.themeParams) {
            val e = Element(k)
            e.setText(v)
            drawing.addContent(e)
        }
        val outputter = XMLOutputter(Format.getPrettyFormat())
        val writer = FileWriter(File(getUserDir(), "config.xml"))
        outputter.output(document, writer)
    }

    @JvmStatic
    fun clearRecentFiles() {
        val e = document!!.rootElement.getChild("recent-files")
        e?.removeChildren("file")
    }

    data class GlobalProperties(var website: String) {
    }

    @JvmStatic
    private fun recoverWebsite() {
        val client: HttpClient = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://raw.githubusercontent.com/fjossinet/RNArtist/master/properties.json"))
                .build()
        try {
            val response: HttpResponse<String> = client.send(request,
                    HttpResponse.BodyHandlers.ofString())
            val properties = Gson().fromJson<GlobalProperties>(response.body() as String, GlobalProperties::class.java)
            website = "http://${properties.website}"
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    var website:String? = null

    @JvmStatic
    var fragmentsLibrary: String?
        get() {
            var e = document!!.rootElement.getChild("fragments-library")
            if (e == null) {
                e = Element("fragments-library")
                e.text = "Non redundant"
                document!!.rootElement.addContent(e)
            }
            return e.textTrim
        }
        set(library) {
            document!!.rootElement.getChild("fragments-library").text = library
        }

    @JvmStatic
    val recentEntries: List<MutablePair<String, String>>
        get() {
            var e = document!!.rootElement.getChild("recent-entries")
            if (e == null) {
                e = Element("recent-entries")
                document!!.rootElement.addContent(e)
            }
            val files: MutableList<MutablePair<String, String>> = ArrayList()
            UPPERFOR@ for (o in e.getChildren("entry")) {
                val entry = o as Element?
                for (f in files) if (f.getLeft() == entry!!.getAttributeValue("id") && f.getRight() == entry.getAttributeValue("type")) {
                    continue@UPPERFOR
                }
                files.add(MutablePair(entry!!.getAttributeValue("id"), entry.getAttributeValue("type")))
            }
            document!!.rootElement.removeContent(e)
            e = Element("recent-entries")
            document!!.rootElement.addContent(e)
            for (f in files) {
                val file = Element("entry")
                file.setAttribute("id", f.getLeft())
                file.setAttribute("type", f.getRight())
                e.addContent(file)
            }
            return files
        }

    @JvmStatic
    fun addRecentEntry(id: String, type: String) {
        var e = document!!.rootElement.getChild("recent-entries")
        if (e == null) {
            e = Element("recent-entries")
            document!!.rootElement.addContent(e)
        }
        val file = Element("entry")
        file.setAttribute("id", id)
        file.setAttribute("type", type)
        val files: List<*> = ArrayList(e.getChildren("entry"))
        if (files.size == 10) {
            e.removeContent(files[files.size - 1] as Element?)
            e.addContent(0, file)
        } else {
            for (o in files) {
                val _f = o as Element
                if (_f.getAttributeValue("id") == id && _f.getAttributeValue("type") == type) {
                    e.removeContent(_f)
                }
            }
            e.addContent(0, file)
        }
    }

    @JvmStatic
    var chimeraPath: String?
        get() {
            var e = document!!.rootElement.getChild("external-viewers")
            if (e == null) {
                val osName = System.getProperty("os.name")
                e = Element("external-viewers")
                e.addContent(Element("chimera-path"))
                document!!.rootElement.addContent(e)
                if (osName.startsWith("Mac OS")) {
                    e.getChild("chimera-path").text = "/Applications/Chimera.app/Contents/MacOS/chimera"
                } else if (osName.startsWith("Windows")) {
                    e.getChild("chimera-path").text = "C:\\Program Files\\Chimera\\bin\\chimera.exe"
                } else {
                    e.getChild("chimera-path").text = "/usr/local/chimera/bin/chimera"
                }
            } else {
                val _e = e.getChild("chimera-path")
                if (_e == null) e.addContent(Element("chimera-path"))
            }
            return document!!.rootElement.getChild("external-viewers").getChild("chimera-path").value
        }
        set(path) {
            document!!.rootElement.getChild("external-viewers").getChild("chimera-path").text = path
        }

    @JvmStatic
    var userID: String?
        get() {
            var e = document!!.rootElement.getChild("userID")
            return if (e == null) null else e.text
        }
        set(userID) {
            var e: Element? = document!!.rootElement.getChild("userID")
            if (e == null) {
                e = Element("userID")
                document!!.rootElement.addContent(e)
            }
            e.addContent(userID)
        }

    @JvmStatic
    fun showHelpToolTip(): Boolean {
        var e = document!!.rootElement.getChild("show-help-tooltip")
        if (e == null) {
            e = Element("show-help-tooltip")
            e.text = "true"
            document!!.rootElement.addContent(e)
        }
        return e.text == "true"
    }

    @JvmStatic
    fun showHelpToolTip(show: Boolean) {
        document!!.rootElement.getChild("show-help-tooltip").text = "" + show
    }

    @JvmStatic
    fun showWelcomeDialog(): Boolean {
        var e = document!!.rootElement.getChild("show-welcome-dialog")
        if (e == null) {
            e = Element("show-welcome-dialog")
            e.text = "true"
            document!!.rootElement.addContent(e)
        }
        return e.text == "true"
    }

    @JvmStatic
    fun showWelcomeDialog(show: Boolean) {
        var e = document!!.rootElement.getChild("show-welcome-dialog")
        if (e == null) {
            e = Element("show-welcome-dialog")
            e.text = "true"
            document!!.rootElement.addContent(e)
        }
        document!!.rootElement.getChild("show-welcome-dialog").text = "" + show
    }

    @JvmStatic
    fun launchChimeraAtStart(): Boolean {
        var e = document!!.rootElement.getChild("launch-chimera")
        if (e == null) {
            e = Element("launch-chimera")
            e.text = "false"
            document!!.rootElement.addContent(e)
        }
        return e.text == "true"
    }

    @JvmStatic
    fun launchChimeraAtStart(launch: Boolean) {
        document!!.rootElement.getChild("launch-chimera").text = "" + launch
    }

    @JvmStatic
    fun useLocalAlgorithms(): Boolean {
        var e = document!!.rootElement.getChild("local-algorithms")
        if (e == null) {
            e = Element("local-algorithms")
            e.text = "false"
            document!!.rootElement.addContent(e)
        }
        return e.text == "true"
    }

    @JvmStatic
    fun useLocalAlgorithms(use: Boolean) {
        document!!.rootElement.getChild("local-algorithms").text = "" + use
    }

    fun getRnartistRelease(): String? {
        return try {
            val format = SimpleDateFormat("MMM dd, yyyy")
            "RNArtist Development Release: " + format.format(Calendar.getInstance().time)
        } catch (e: java.lang.Exception) {
            null
        }
    }
}