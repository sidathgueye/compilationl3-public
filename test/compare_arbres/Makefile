CC = gcc
FLEX = flex

LIBS = -lm 
CCFLAGS = -Wall -ggdb

all: compare_arbres_xml

compare_arbres_xml: compare_arbres_xml.c analyseur_xml.o
	$(CC) $(CCFLAGS) -o compare_arbres_xml compare_arbres_xml.c analyseur_xml.o

# Règle générique pour générer les fichiers objet .o à partir des .c
%.o: %.c
	$(CC) $(CCFLAGS) -c $^

.PHONY : clean

clean:
	- rm -f *\.o compare_arbres_xml
