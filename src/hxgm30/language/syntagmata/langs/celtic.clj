from anamyeter.languages import base


class ProtoCeltic(base.Language):
    # no such thing; we made it up
    iso639 = "pcl"


class Gaulish(base.Language):
    # no such thing; we made it up
    iso639 = "gau"


class OldIrish(base.Language):
    iso639 = "sga"


class Irish(base.Language):
    iso639 = "gle"


class ScotsGaelic(base.Language):

    implements(ILanguage)
    name = "Scots Gaelic"
    iso639 = "gla"

    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata(self.iso639)
