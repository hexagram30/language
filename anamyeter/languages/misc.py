class Onomatopoetic(Language):
    implements(ILanguage)
    # note that there is none for this, we made it up
    iso639 = "ono"
    def __init__(self):
        super(Onomatopoetic, self).__init__()
        self.language = Syntagmata('onomatopoetic')
