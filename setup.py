from setuptools import find_packages, setup


setup(name="Anamyéter",
    version="2.0",
    description="A Python word and name generator",
    author="Duncan McGreggor",
    author_email="duncan@twistedmatrix.com",
    url="https://github.com/oubiwann/anamyeter",
    packages=find_packages(),
    # XXX find a good home for these... maybe ~/.anamyeter?
    #data_files=[('wordgen/corpora',
    #    ['wordgen/corpora/afrikaner_wordlist.txt',
    #    'wordgen/corpora/arabic_wordlist.txt',
    #    'wordgen/corpora/chinese_wordlist.txt',
    #    'wordgen/corpora/english_wordlist.txt',
    #    'wordgen/corpora/french_wordlist.txt',
    #    'wordgen/corpora/german_wordlist.txt',
    #    'wordgen/corpora/hindi_wordlist.txt',
    #    'wordgen/corpora/japanese_wordlist.txt',
    #    'wordgen/corpora/korean_wordlist.txt',
    #    'wordgen/corpora/russian_wordlist.txt',
    #    'wordgen/corpora/spanish_wordlist.txt']
    )],
)
