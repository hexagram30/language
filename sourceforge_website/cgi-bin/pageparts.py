#!/usr/bin/python

# first, we want to set this up so that any errors
# that occur, we see right away
import sys
sys.stderr = sys.stdout
print "Content-Type: text/html\n\n"
#print "<pre>"

import cgi
import re
from string import split, strip, join

# store the cgi params in a dict
def cgiParams(fieldStorage):
  params = {}
  for key in fieldStorage.keys():
    params[key] = fieldStorage[key].value
  return params

# function that gets the vertical menus
def getMenuVert(filename):
  file = open(filename).readlines()
  url = menu = text = line = ''
  for line in file:
    if re.match("^-{4}$", line):
      menu = menu + re.sub("^-{4}$", "<hr>", line)
    else:
      parts = split(line, "|")
      text = strip(parts[0])
      try:
        url = strip(parts[1])
      except:
        url = ''

      # check for a 'heading' entry in the menu flat-text file
      if re.match("^Heading:", line):
        text = getContentPart('Heading', line, '<h3>', '</h3>')

      # check for a 'subheading' entry in the menu flat-text file
      if re.match("^Subheading:", line):
        text = getContentPart('Subeading', line, '<h4>', '</h4>')

      # build the html for the menu entry
      if url:
        if not re.match("^http://", url) and re.match(".+[.].+", url):
          url ="http://" + url
        menu = menu + '<a href="%s">%s</a><br/>' % (url, text)
      else:
        menu = menu + text
  return menu

def getMenuHoriz(filename):
  file = open(filename).readlines()
  url = text = line = ''
  menu = []
  for line in file:
    parts = split(line, "|")
    text = strip(parts[0])
    try:
      url = strip(parts[1])
    except:
      url = ''
    # build the html for the menu entry
    if url:
      if not re.match("^http://", url) and re.match(".+[.].+", url):
        url ="http://" + url
      menu.append('<a href="%s" class="small">%s</a>' % (url, text))
    else:
      menu.append(text)
  return join(menu, " | ")

def getContentPart(key, content, stag='', etag=''):
  pattern = "^(%s:)(.*)$" % (key)
  regex = re.compile(pattern)
  content = regex.sub("%s\g<2>%s" % (stag, etag), content)
  return strip(content)

def getContent(filename, format=''):
  file = open(filename).readlines()
  story = ''
  for line in file:
    if re.match("^Section:", line):
      story = story + getContentPart('Section', line, '<h2>', '</h2>')
    elif re.match("^Headline:", line):
      story = story + getContentPart('Headline', line, '<h3>', '</h3>')
    elif re.match("^Date:", line):
      story = story + getContentPart('Date', line, '<p><span class="newsdate">[', ']</span>&nbsp;&nbsp;')
    elif re.match("^Description:", line):
      story = story + getContentPart('Description', line, '<b>', '</b><br/><br/>')
    elif re.match("^Content:", line):
      story = story + getContentPart('Content', line)
    elif not re.match("^Month:", line):
      story = story + line
  return story

def getSideBar(filename):
  file = open(filename).readlines()
  bar = ''
  for line in file:
    if re.match("^Month:", line):
      bar = bar + getContentPart('Month', line, '<h4 class="eventmonth">', '</h4>')
    if re.match("^Headline:", line):
      bar = bar + getContentPart('Headline', line, '<h4>', '</h4>')
  return bar

def getConfig(filename):
  return strip(open(filename).readlines()[0])

#######################################################################
# main
######

# setup the passed variables
cgiinput = cgi.FieldStorage()
params = cgiParams(cgiinput)
datatype = params['datatype']
file = params['file']
try:
  format = params['format']
except:
  format = ''

# open the requested file and load it as a string
filename = "../data/%s/%s" % (datatype, file)

# print the output
if datatype == 'menu.vert':
  print getMenuVert(filename)
if datatype == 'content':
  if format == 'sidebar':
    print getSideBar(filename)
  else:
    print getContent(filename, format)
if datatype == 'menu.horiz':
  print getMenuHoriz(filename)
if datatype == 'config':
  print getConfig(filename)  
