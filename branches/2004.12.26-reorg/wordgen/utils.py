import codecs

from exceptions import FileNotFound

class Counter(dict):

    def updateCount(self, key):
        try:
            self[key] += 1
        except KeyError:
            self.setdefault(key, 1)

        
class UTF8File(object):
    
    def __init__(self, filename=None, mode='r'):
        self.filename = filename
        if filename:
            self.open(filename, mode)

    def open(self, filename, mode):
        if filename:
            self.fh = codecs.open(filename, mode, 'utf-8')
            return self
        raise FileNotFound
    
    def write(self, data):
        self.fh.write(data)
    
    def read(self):
        return self.fh.read()

    def readlines(self):
        return self.fh.readlines()

    def close(self):
        self.fh.close()

