class English(Language):
    implements(ILanguage)
    def __init__(self):
        super(English, self).__init__()
        self.language = Syntagmata('english')


class Afrikaner(Language):
    implements(ILanguage)
    def __init__(self):
        super(Afrikaner, self).__init__()
        self.language = Syntagmata('afrikaner')


class German(Language):
    implements(ILanguage)
    def __init__(self):
        super(German, self).__init__()
        self.language = Syntagmata('german')


class OldNorse(Language):
    implements(ILanguage)
    def __init__(self):
        super(OldNorse, self).__init__()
        self.language = Syntagmata('oldnorse')


class OldEnglish(Language):
    implements(ILanguage)
    def __init__(self):
        super(OldEnglish, self).__init__()
        self.language = Syntagmata('oldenglish')
