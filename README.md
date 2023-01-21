# Information-Retrieval-Project
Information Retrieval project for multimedia information retrieval and computer vision course in Artificial and Data engineering course at University of Pisa

The aim of the project was to write a program that creates an index structure from a set of text documents,
and a program that process queries over such index.

<h2>What the project can do</h2>
<b>Indexing:</b> Given a document collection in a zip format, the indexing component creates
an efficient index structure composed of a lexicon, an inverted index, a document index, some collection statistics and a structure to implement skipping.

<b>Query processing:</b> Given a query, the query processing component perform a query against the indexed collection, returning the top k
results according to the specified scoring function. At the moment the implemented scoring function are TFIDF and BM25.

<b>Query evaluation:</b> Given a query file in the trec_eval format, the evaluation component return a query results file that can be used with relative
qrels file with trec_eval to evaluate the performance of the search engine.

<h2>How to use the program</h2>
The program is composed of 3 components: indexing, query processing and evaluation.
For every component is present the corresponding jar file in the main directory of the project.
For this project as document collection we used the msmarco document collection: https://msmarco.blob.core.windows.net/msmarcoranking/collection.tar.gz.

<h3>Indexing</h3>
In order to perform indexing you need to use the Indexing.jar artifact passing as arguments: 
<ol>
    <li>The path to zipped document collection</li>
    <li>The encoding type to use: it can be "text" to use ASCII encoding (useful for debugging) or "bytes" to have a compressed byte encoding (performance)</li>
    <li>A boolean value to select if performing stopwords removal: it can be "true" or "false"</li>
    <li>A boolean value to select if performing stemming: it can be "true" or "false"</li>
</ol>

An example of command can be: java -jar Indexing.jar Data/Input/collection.tsv.zip bytes true true.
The results of the Indexing will be stored in the Data/Output folder.

<h3>Query processing</h3>
To perform query processing you need to use the QueryProcessing artifact passing as arguments: 
<ol>
    <li>The number of document to return</li>
    <li>The scoring function to use: "tfidf" or "bm25"</li>
    <li>The algorithm used to perform query processing: it can be "daat" or "maxscore"</li>
    <li>A string representing if performing a conjunctive or disjunctive retrieval: it can be "conjunctive" or "disjunctive"</li>
    <li>A boolean value to select if performing stopwords removal: it can be "true" or "false"</li>
    <li>A boolean value to select if performing stemming: it can be "true" or "false"</li>
</ol>
An example of command can be: java -jar QueryProcessing.jar 10 tfidf daat disjunctive true true
<h3>Evaluation</h3>
To perform evaluation you need to use the Evaluation.jar artifact passing as arguments:
<ol>
    <li>The path to the file containing the test queries.</li>
</ol>
An example of command can be: java -jar Evaluation.jar Data/Input/queries.dev.small.tsv.
For debugging purpose the program will output the result to every query.




