To check and fix your script syntax, type the following command in your terminal:

java -jar rnartist_core.jar -c dsl_script_file

The option ```-c``` check the syntax of the script before to run it. It prints the issues found.

### 0.2.22-SNAPSHOT

- the element ```color``` can contain an attribute named ```scheme``` to use predefined color schemes.

```kotlin
rnartist {
    theme {
        color {
            scheme = "Persian Carolina"
        }
    }
}
```

### 0.2.21-SNAPSHOT

- to avoid to mix the description of a 2D with other elements, details for a 2D are inside a element named ```parts``` 

```kotlin
//before
rnartist {
    ss {
        rna {
            seq =
                "ACAUAGCGUUCGCGCGUGUUCCUGUAGUUAAACUUAGAGUAUCUGUACUUAGAAUUAAUGUUGGAGGCCCAACAAUGGGUGUGGAUCAAUCGUAGUUAUUU"
            name = "my RNA"
        }
        helix {
            name = "H1"
            location {
                1 to 3
                17 to 19
            }
        }
    }
}

//now

rnartist {
    ss {
        parts {
            rna {
                seq =
                    "ACAUAGCGUUCGCGCGUGUUCCUGUAGUUAAACUUAGAGUAUCUGUACUUAGAAUUAAUGUUGGAGGCCCAACAAUGGGUGUGGAUCAAUCGUAGUUAUUU"
                name = "my RNA"
            }
            helix {
                name = "H1"
                location {
                    1 to 3
                    17 to 19
                }
            }
        }
    }
}
```

### 0.2.20-SNAPSHOT

- if the 2D has been computed from a PDB file, the new element chimera allows to export the 2D theme (residue colors) as a chimera script (cxc file). Combined with the elements ```svg``` and/or ```png```, this allows to have 2D and 3D pictures with the same coloring scheme.

```kotlin
rnartist {
    chimera {
        path = "my_output_dir/"
    }

    svg {
        path = "my_output_dir/"
    }

}
```

### 0.2.19-SNAPSHOT

- the element ```rfam``` can contain an element ```rna``` and at least one element ```helix``` to describe a secondary structure.

```kotlin
rnartist {

  ss {
    rna {
      seq = "ACAUAGCGUUCGCGCGUGUUCCUGUAGUUAAACUUAGAGUAUCUGUACUUAGAAUUAAUGUUGGAGGCCCAACAAUGGGUGUGGAUCAAUCGUAGUUAUUU"
      name = "my RNA"
    }
    helix {
      name = "H1"
      location {
        1 to 3
        17 to 19
      }
    }
    helix {
      name = "H2"
      location {
        6 to 8
        13 to 15
      }
    }
    helix {
      name = "H3"
      location {
        23 to 25
        39 to 41
      }
    }
    helix {
      name = "H4"
      location {
        28 to 30
        35 to 37
      }
    }
    helix {
      name = "H5"
      location {
        45 to 47
        80 to 82
      }
    }
    helix {
      name = "H6"
      location {
        48 to 50
        64 to 66
      }
    }
    helix {
      name = "H7"
      location {
        53 to 55
        60 to 62
      }
    }
    helix {
      name = "H8"
      location {
        69 to 71
        76 to 78
      }
    }
    helix {
      name = "H9"
      location {
        83 to 85
        99 to 101
      }
    }
    helix {
      name = "H10"
      location {
        88 to 90
        95 to 97
      }
    }
  }
}
```

### 0.2.18-SNAPSHOT

- the element ```rfam``` can contain an attribute named ```use alignment numbering```. If this attribute is set, the locations described in the script will be understood as locations in the original alignment. Check this [video](https://www.youtube.com/watch?v=cEFlneO_muE) for details

```kotlin
rnartist {
    ss {
        rfam {
            id = "RF02500"
            name = "AAUW01000008.1/93016-93126"
            use alignment numbering
        }
    }
}

```


### 0.2.17-SNAPSHOT

- **details_lvl in theme**: not anymore an attribute of ```theme```. It is now an element named ```details```
```kotlin
//before
rnartist {
    theme {
        details_lvl = 3
    }
}

//now 
rnartist {
    theme {
        details {
            value = 3
        }
    }
}

```

Using the attribute ```location```, it is now possible to link different levels of details to different parts of the 2D. Without this attribute, the level of details is applied to the full 2D. The details levels are applied one after other. 

```kotlin
//before
rnartist {
    theme {
        details {
            value = 2
        }
        details {
            value = 3
            location {
                50 to 53
                55 to 60
                62 to 64
            }
        }
        details {
            value = 1
            location {
                25 to 28
                37 to 39
            }
        }
    }
}
```

Using the attribute ```type```, you can quickly apply details levels to all the helices, junctions and/or single-strands

```kotlin
//before
rnartist {
    theme {
        details {
            value = 1
        }
        details {
            value = 3
            type = "helix"
        }
        details {
            value = 2
            type = "junction"
        }
    }
}
```


### 0.2.15-SNAPSHOT

- **define an RNA 2D with a bracket notation**: the bracket notation becomes an element ```bn```. It is at the same level that the input file formats (```vienna```, ```bpseq```,...). Instead to precise the filename, you need to provide the bracket notation. If no sequence is set, a random one is generated, fitting the base-pairing constraints. The default name for the sequence is 'A'. 
```kotlin
//before
rnartist {
  rna {
    sequence = "CAACAUCAUACGUACUGCGCCCAAGCGUAACGCGAACACCACGAGUGGUGACUGGUGCUUG"
  }
  bracket_notation =
    "(((..(((..(((..(((((....)))))..)))..(((((....)))))..)))...)))"
}

//now
rnartist {
    ss {
        bn {
            seq = "CAACAUCAUACGUACUGCGCCCAAGCGUAACGCGAACACCACGAGUGGUGACUGGUGCUUG"
            name = "my_rna"
            value =
                "(((..(((..(((..(((((....)))))..)))..(((((....)))))..)))...)))"
        }
    }
}
```
- **2D plot saving in PNG or SVG file for the drawing algorithm ```rnartist```**: the file format is now the name of an element (```svg``` or ```png```) containing the saving path. The name of the RNA molecule exported is used for the filename. 
```kotlin
//before
rnartist {
  file = "media/real_example.svg"
}

//now
rnartist {
  svg {
    path = "media/"
  }
  
  ss {
    bn {
        value = "(((...)))"
        name = "real_example"
    }
  }
}
```



