import codecs

from wordgen.exceptions import FileNotFound


class Counter(dict):

    def updateCount(self, key):
        try:
            self[key] += 1
        except KeyError:
            self.setdefault(key, 1)


class UTF8File(object):
    
    def __init__(self, filename='', mode='r'):
        self.filename = filename
        if filename:
            self.open(filename, mode)

    def open(self, filename='', mode='r'):
        if not filename:
            filename = self.filename
        if not filename:
            raise FileNotFound
        self.fh = codecs.open(filename, mode, 'utf-8')
        return self
    
    def write(self, data):
        self.fh.write(data)
    
    def read(self):
        return self.fh.read()

    def readlines(self):
        return self.fh.readlines()

    def close(self):
        self.fh.close()
