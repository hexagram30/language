class Spanish(Language):
    implements(ILanguage)
    def __init__(self):
        super(Spanish, self).__init__()
        self.language = Syntagmata('spanish')


class Latin(Language):
    implements(ILanguage)
    def __init__(self):
        super(Latin, self).__init__()
        self.language = Syntagmata('latin')


class French(Language):
    implements(ILanguage)
    def __init__(self):
        super(French, self).__init__()
        self.language = Syntagmata('french')
