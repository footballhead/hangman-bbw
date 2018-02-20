"""
Sort a flat dictionary file into "easy"/"normal"/"hard" Hangman words.

The usage is as follows:

	python sortwords.py ../raw_assets/sowpods.txt

This will produce 6 files:

 1. easy.txt -- A list of "easy" words, one per line
 2. easyh.txt -- The number of words in easy.txt
 3. normal.txt -- A list of "normal" words, one per line
 4. normalh.txt -- The number of words in normal.txt
 5. hard.txt -- A list of "hard" words, one per line
 6. hardh.txt -- The number of words in hard.txt

These files should be copied into `app/src/main/res/raw` to be included with the
app.

Difficulty is based on two things:

1. The number of unique characters
2. The presense of uncommon characters like q/x/z

See the is*() function for the arbitrary definition of each difficulty.
"""

import sys

def numUniqueChars( word ):
	"""
	Determine how many letters of the alphabet are present in a given word.

	@param word An English word.
	@returns The number of unique letters in that word.
	"""
	seen = ['\n']
	count = 0

	for c in word:
		if c not in seen:
			count += 1
			seen.append( c )

	return count

def isEasy( word, unique ):
	"""
	Determine if a word is "easy", that is it has more than 9 letters of the
	alphabet but doesn't include q/x/z

	@param word The English word to calculate the difficulty of
	@param unique The number of unique letters in that word
	@returns true if t matches our definition of "easy", false otherwise
	"""
	if 'z' in word or 'x' in word or 'q' in word:
		return False

	return unique >= 9

def isNormal( word, unique ):
	"""
	Determine if a word is "normal", that is it has 5-8 letters of the alphabet,
	or it includes q/x/z

	@param word The English word to calculate the difficulty of
	@param unique The number of unique letters in that word
	@returns true if t matches our definition of "normal", false otherwise
	"""
	if 'z' in word or 'x' in word or 'q' in word:
		return unique >= 5

	return unique <= 8 and unique >=5

def isHard( word, unique ):
	"""
	Determine if a word is "hard", that is it has less than 4 letters of the
	alphabet, or it includes q/x/z

	@param word The English word to calculate the difficulty of
	@param unique The number of unique letters in that word
	@returns true if t matches our definition of "hard", false otherwise
	"""
	return unique <= 4

def printListToFile( lst, filename ):
	"""
	Dump a list of words to a file.

	@param lst The list of words
	@param filename The file to write the list to.
	"""
	print( "Printing to %s..." % filename )
	with open( filename, "w" ) as f:
		for word in lst:
			f.write( word );

def printCountToFile( lst, filename ):
	"""
	Dump the count of words to a file.

	@param lst The number of words
	@param filename The file to write the number to
	"""
	print( "Printing count to %s..." % filename )
	with open( filename, "w" ) as f:
		f.write( str( len( lst ) ) )


def main():
	if len( sys.argv ) < 2:
		print( "Usage: python %s [dict-file]" % sys.argv[0] )
		return 1

	easyWords = []
	normalWords = []
	hardWords = []
	dictFileName = sys.argv[1]

	with open( dictFileName ) as wordsfile:
		print( "Parsing and sorting %s..." % dictFileName )

		line = wordsfile.readline()
		while line:
			count = numUniqueChars( line )

			if isEasy( line, count ):
				easyWords.append( line )
			elif isNormal( line, count ):
				normalWords.append( line )
			elif isHard( line, count ):
				hardWords.append( line )
			else:
				print( "WARNING: `%s` unclassified!" % line )

			line = wordsfile.readline()

	printListToFile( easyWords, "easy.txt" )
	printCountToFile( easyWords, "easyh.txt" )
	printListToFile( normalWords, "normal.txt" )
	printCountToFile( normalWords, "normalh.txt" )
	printListToFile( hardWords, "hard.txt" )
	printCountToFile( hardWords, "hardh.txt" )

	print( "Done" )

	return 0

sys.exit( main() )