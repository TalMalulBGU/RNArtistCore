rnartist {
    ###OUT_FILE_FORMAT### {
        path = "###OUT_FILE_LOCATION###"
    }
    ss {
        bn {
            seq = "###MOLECULE###"
            value = "###BRACKETS###"
            name = "###OUT_FILENAME###"
        }
    }
    theme {
        details {
			value = 4
		}
        color {
            value = "#800080"
            type = "N"
            location {
            21 to 50
            }
        }
        color {
            value = "#ffcc80"
            type = "N"
            location {
            0 to 20
            }
        }
        color {
            value = "#A0ECF5"
            type = "N"
            location {
            69 to 80
            }
        }
        color {
            value = "#99cc99"
            type = "N"
            location {
            82 to 96
            }
        }
		layout {
			junction {
				type = 3
				out_ids = "wnw n"
			}
		}
	}
}