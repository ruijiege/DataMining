import sys

# read all word into array
word_list = [word for line in open(sys.argv[1], 'r') for word in line.split()]
unique_word_list = list(set(word_list))

# reconstruct file
fh = open('converted_'+sys.argv[1], 'w')
i = 0
for word in word_list:
	i += 1
	fh.write(str(unique_word_list.index(word)))
	if i%2 == 1:
		fh.write(' ')
	else:
		fh.write('\n')