%include	'io.asm'

section	.bss
sinput:	resb	255	;reserve a 255 byte space in memory for the users input string
a :	resd	4

section	.text
global _start
_start:
	call	main	;
	mov	ebx,	0	; valeur de retour du programme
	mov	eax,	1	;
	int 0x80	;
main :	push	ebp	;sauvegarde la valeur de ebp
	mov	ebp,	esp	;nouvelle valeur de ebp
	sub	esp,	0	;allocation des variables locales
	mov	dword [a],	10	;Affect
l0 :	mov	ebx,	1	;Affect
	mov	eax,	2	;JumpIfLess 1
	cmp	eax,	dword [a]	;on passe par un registre temporaire
	jl	l2	;JumpIfLess 2
	mov	ebx,	0	;Affect
l2 :	cmp	ebx,	0	;JumpIfEqual 1
	je	l1	;JumpIfEqual 2
	mov	eax,	dword [a]	;Write 1
	call	iprintLF	;Write 2
	mov	ebx,	dword [a]	;
	sub	ebx,	1	;
	mov	dword [a],	ebx	;Affect
	jmp	l0	;Jump
l1 :	add	esp,	0	;désallocation des variables locales
	pop	ebp	;restaure la valeur de ebp
	ret	;
