/**
 * This script recovers Rfam entries and produces 2D drawing based on both drawing algorithms (Booquet and the one used in the RNArtist app)
 * Create an rnartistcore project with the script rnartistcore.sh (see README for details). Copy this script in the project directory and run it like:
 * ./plot_2ds.sh fetchRfam.kts
 * All the output files will be stored in the project directory.
 */

import io.github.fjossinet.rnartist.core.booquet
import io.github.fjossinet.rnartist.core.rnartist

(1..100).forEach {
    val rfamID = "RF${"%05d".format(it)}"
    println(rfamID)
    try {
        booquet {
            file = "/project/${rfamID}_booquet.svg"
            junction_diameter = 15.0
            ss {
                rfam {
                    id = rfamID
                    name="consensus"
                }
            }
        }
        rnartist {
            svg {
                path = "./"
            }
            ss {
                rfam {
                    id = rfamID
                    name = "consensus"
                }
            }
            theme {
                details {
                    value = 5
                }

                color {
                    type = "R"
                    value = "deepskyblue"
                }

                color {
                    type = "Y"
                    value = "darkgreen"
                }
            }
        }
    } catch (e: Exception) {
        println("Exception for $rfamID: ${e.message}")
    }
}

