%include	'io.asm'

section	.bss
sinput:	resb	255	;reserve a 255 byte space in memory for the users input string

section	.text
global _start
_start:
	call	main	;
	mov	ebx,	0	; valeur de retour du programme
	mov	eax,	1	;
	int 0x80	;
procedure :	push	ebp	;sauvegarde la valeur de ebp
	mov	ebp,	esp	;nouvelle valeur de ebp
	sub	esp,	8	;allocation des variables locales
	mov	dword [ebp-4*1],	2	;Affect
	mov	dword [ebp-4*2],	2	;Affect
	mov	eax,	dword [ebp-4*1]	;
	add	eax,	dword [ebp-4*2]	;
	mov	dword [ebp+4*2],	eax	;ecriture de la valeur de retour
	add	esp,	8	;désallocation des variables locales
	pop	ebp	;restaure la valeur de ebp
	ret	;
main :	push	ebp	;sauvegarde la valeur de ebp
	mov	ebp,	esp	;nouvelle valeur de ebp
	sub	esp,	0	;allocation des variables locales
	sub	esp,	4	;allocation mémoire pour la valeur de retour
	call	procedure	;
	pop	eax	;récupération de la valeur de retour
	mov	eax,	eax	;Write 1
	call	iprintLF	;Write 2
	add	esp,	0	;désallocation des variables locales
	pop	ebp	;restaure la valeur de ebp
	ret	;
