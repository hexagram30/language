"""
This module defines a way of extracting the corpora text files from the python
library paths.
"""
basePath = './corpora'
vowelFileTmpl = basePath + "/vowels/%s.txt"
consonantFileTmpl = basePath + "/consonants/%s.txt"
statsFileTmpl = basePath + "/stats/%s.txt"
sourceFileTmpl = basePath + "/sources/%s.txt"

class Corpus(object):
    """
    This object acts as a proxy for textual data files in the corpora,
    specifically, for one language at a time. All of that languages textual
    resources are exposed as attributes of the Corpus object.
    """
    vowels = None
    consonants = None
    source = None
    stats = None

