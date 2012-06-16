from anamyeter.languages import base


class ProtoCeltic(base.Language):
    pass


class Gaulish(base.Language):
    pass


class OldIrish(base.Language):
    pass


class Irish(base.Language):
    pass


class ScotsGaelic(base.Language):

    implements(ILanguage)
    name = "Scots Gaelic"
    abbrev = ""

    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata('gaelic')
