# WebCrawler and Search Engine

WebCrawler crawls a portion of the web to build an index that allows you to quickly access portions of this web, and to respond
to various types of queries—or web searches—much like Google queries. For example, you should be able to type
“hoojiedoober” and get a list of pages that contain the word “hoojiedoober.”

## CRAWLER INSTRUCTIONS

First, make sure you add the attoparser library to your classpath! Then, you can run the WebCrawler
class, providing it ABSOLUTE URLS, on the two provided testing websites. Absolute URLS are prefixed
by "file://", and look something like this:

file://\<absolute-path-to-file\>

For example, on the command line, using linux:

java -cp attoparser-2.0.0.BETA2.jar:bin assignment.WebCrawler file:///\<path-to-project\>/president96/index.html

This should run your crawler and save an index to "index.db"!

## WEBSERVER INSTRUCTIONS

To run the webserver, run the assignment.WebServer class; this will load your previously generated
index.db file from your crawler, and then set up an HTTP webserver you can connect to in your
browser:

java -cp attoparser-2.0.0.BETA2.jar:bin assignment.WebServer

The program will output "listening on port 1989"; to see the actual website, go to

localhost:1989

in your browser; the UI should show up promptly.

## QUERY TYPES

### Basic Queries

This consists of individual words, the logical AND (&) operator, the logical OR (|) operator, and parentheses. To simplify the parsing, your language will have to fully parenthesize each query, ie, any use of AND or OR requires a set of parentheses. Here are some examples of basic queries:
- snufflelupagus
  - Find pages that contain the word “snufflelupagus.”
- (rosencrantz & guildenstern)
  - Find pages that contain both “rosencrantz” and “guildenstern.”
- (naughty | bear)
  - Find pages that contain either “naughty” or “bear.”
- ((wealth & fame) | happiness)
  - Find pages that contain both “wealth” and “fame” or pages that contain “happiness.”

### Negative Queries

The NOT operator (!) matches pages that do not contain the specified word.

### Phrase Queries

Phrase queries search for a contiguous sequence of words. The phrase is indicated by surrounding a sequence of words in double quotation marks, for example, "john paul george".

### Implicit AND Queries

If a query consists of consecutive words (not in quotation marks), the engine searches for pages that contain both words. 

### Grammar/Examples 

This is the specific grammar that the parser follows. All valid queries will follow this 

Query → Query’ Query\
Query → Query'\
Query’ → ( Query’ & Query’ )\
Query’ → ( Query’ | Query’ )\
Query’ → word\
Query’ → !word\
Query’ → "Words"\
Words → word Words\
Words → word

For example, foo bar | baz isn’t a valid query since the OR is not parenthesized.
