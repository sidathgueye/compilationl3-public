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
main :	push	ebp	;sauvegarde la valeur de ebp
	mov	ebp,	esp	;nouvelle valeur de ebp
	sub	esp,	0	;allocation des variables locales
	mov	ecx,	2	;
	imul	ecx,	3	;
	mov	ebx,	1	;
	add	ebx,	ecx	;
	mov	ecx,	1	;Affect
	cmp	ebx,	0	;JumpIfLess 1
	jl	l2	;JumpIfLess 2
	mov	ecx,	0	;Affect
l2 :	mov	eax,	2	;
	add	eax,	2	;
	mov	ebx,	1	;Affect
	cmp	eax,	0	;JumpIfLess 1
	jl	l3	;JumpIfLess 2
	mov	ebx,	0	;Affect
l3 :	cmp	ecx,	0	;JumpIfEqual 1
	je	l1	;JumpIfEqual 2
	cmp	ebx,	0	;JumpIfEqual 1
	je	l1	;JumpIfEqual 2
	mov	eax,	1	;Affect
	jmp	l0	;Jump
l1 :	mov	eax,	0	;Affect
l0 :	mov	eax,	eax	;Write 1
	call	iprintLF	;Write 2
	add	esp,	0	;désallocation des variables locales
	pop	ebp	;restaure la valeur de ebp
	ret	;
