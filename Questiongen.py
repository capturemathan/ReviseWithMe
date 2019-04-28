from flask import Flask,jsonify
from textblob import TextBlob
import nltk
from textblob import Word
import sys


app = Flask(__name__)

@app.route('/<string>')
def parse(string):
	line = string
	questions=""
	try:
		txt = TextBlob(string)
		questions=""
		for sentence in txt.sentences:
			if type(sentence) is str:
				sentence = TextBlob(sentence)
			bucket={}

			for i,j in enumerate(sentence.tags):  
				if j[1] not in bucket:
					bucket[j[1]] = i  
			
			question = ''

			# WHAT CLAUSE QUESTIONS.

			l1 = ['NNP', 'VBG', 'VBZ', 'IN']
			l2 = ['NNP', 'VBG', 'VBZ']
			l3 = ['PRP', 'VBG', 'VBZ', 'IN']
			l4 = ['PRP', 'VBG', 'VBZ']
			l5 = ['NN', 'VBG', 'VBZ']
			l6 = ['NNP', 'VBZ', 'JJ']
			l7 = ['NNP', 'VBZ', 'NN']
			l8 = ['NNP', 'VBZ']
			l9 = ['PRP', 'VBZ']
			l10 = ['NN', 'VBZ']

			# WHERE CLAUSE QUESTIONS.

			wh1= ['NNP','VBZ','IN']

			# WHO CLAUSE QUESTIONS.

			p1=['NNP','VBZ','DT','NN']
			p2=['NNP','VBZ','DT','NN','IN']

			# HOW CLAUSE QUESTIONS.

			h1 =['NN','VBN','IN']
			h2 =['NN','VBD','IN']

			# NUMBER [QUANTITATIVE INFO]

			v1=['CD','NN']
			v2=['CD','NNS']
		
			if all(key in bucket for key in p1):
				question = 'Who' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NNP']]+ '?'

			elif all(key in  bucket for key in p2):
				question = 'Who' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NNP']]+ '?'

			elif all(key in  bucket for key in wh1):
				question = 'Where' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NNP']]+ '?'

			elif all(key in  bucket for key in h1):
				question = 'How' + ' ' + sentence.words[bucket['NN']] +' '+ sentence.words[bucket['VBN']]+ '?'

			elif all(key in  bucket for key in h2):
				question = 'How' + ' ' + sentence.words[bucket['NN']] +' '+ sentence.words[bucket['VBD']]+ '?'

			elif all(key in  bucket for key in v1):
				question = 'How' + ' ' +'many ' + sentence.words[bucket['NN']] + '?'

			elif all(key in  bucket for key in v2):
				question = 'How' + ' ' +'many ' + sentence.words[bucket['NNS']] + '?'

			elif all(key in  bucket for key in l1):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NNP']]+ ' '+ sentence.words[bucket['VBG']] + '?'

			elif all(key in  bucket for key in l2):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NNP']] +' '+ sentence.words[bucket['VBG']] + '?'

			elif all(key in  bucket for key in l3):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['PRP']]+ ' '+ sentence.words[bucket['VBG']] + '?'

			elif all(key in  bucket for key in l4):
				question = 'What ' + sentence.words[bucket['PRP']] +' '+  ' does ' + sentence.words[bucket['VBG']]+ ' '+  sentence.words[bucket['VBG']] + '?'

			elif all(key in  bucket for key in l5):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] +' '+ sentence.words[bucket['NN']] +' '+ sentence.words[bucket['VBG']] + '?'

			elif all(key in bucket for key in l6):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] + ' ' + sentence.words[bucket['NNP']] + '?'

			elif all(key in bucket for key in l7):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] + ' ' + sentence.words[bucket['NNP']] + '?'

			elif all(key in bucket for key in l9):
				if sentence.words[bucket['PRP']] in ['she','he']:
					question = 'What' + ' does ' + sentence.words[bucket['PRP']].lower() + ' ' + sentence.words[bucket['VBZ']].singularize() + '?'

			elif all(key in bucket for key in l8):
				question = 'What' + ' does ' + sentence.words[bucket['NNP']] + ' ' + sentence.words[bucket['VBZ']].singularize() + '?'

			elif all(key in bucket for key in l10):
				question = 'What' + ' ' + sentence.words[bucket['VBZ']] + ' ' + sentence.words[bucket['NN']] + '?'

			if 'VBZ' in bucket and sentence.words[bucket['VBZ']] == "’":
				question = question.replace(" ’ ","'s ")

			questions+=question

		return jsonify(q = questions)

	except Exception as e:
		raise e
	

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=5000)
        