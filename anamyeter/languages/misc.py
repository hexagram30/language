class Onomatopoetic(Language):
    implements(ILanguage)
    def __init__(self):
        super(Onomatopoetic, self).__init__()
        self.language = Syntagmata('onomatopoetic')
