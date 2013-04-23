	.global main

	.text
main:
	save	%sp, -96, %sp
L4:
	mov	0, %o0
	mov	4, %o1
	call calloc
	nop
	mov	%o0, %l0
	mov	10, %o1
	call Fac$ComputeFac
	nop
	mov	%o0, %l1
	call printInt
	nop
	ba	L3
	nop
L3:
	ret
	restore

Fac$ComputeFac:
	save	%sp, -96, %sp
L6:
	cmp	%i1, 1
	bl L0
	nop
L1:
	mov	%i1, %l0
	sub	%i1, 1, %o1
	call Fac$ComputeFac
	nop
	mov	%o0, %l1
	smul	%l0, %l1, %l2
	mov	%l2, %l3
L2:
	mov	%l3, %i0
	ba	L5
	nop
L0:
	mov	1, %l3
	ba	L2
	nop
L5:
	ret
	restore

