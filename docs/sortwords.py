import sys

def numUniqueChars( word ):
	seen = ['\n']
	count = 0

	for c in word:
		if c not in seen:
			count += 1
			seen.append( c )

	return count

def isEasy( word, unique ):
	if 'z' in word or 'x' in word or 'q' in word:
		return False

	return unique >= 9

def isNormal( word, unique ):
	if 'z' in word or 'x' in word or 'q' in word:
		return unique >= 5

	return unique <= 8 and unique >=5

def isHard( word, unique ):
	return unique <= 4

def printListToFile( lst, filename ):
	print( "Printing to %s..." % filename )
	with open( filename, "w" ) as f:
		for word in lst:
			f.write( word );

def printCountToFile( lst, filename ):
	print( "Printing count to %s..." % filename )
	with open( filename, "w" ) as f:
		f.write( str( len( lst ) ) )


def main():
	easyWords = []
	normalWords = []
	hardWords = []

	with open( "words.txt" ) as wordsfile:
		print( "Parsing and sorting words.txt..." )

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