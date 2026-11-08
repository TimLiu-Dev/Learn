	.build_version macos, 26, 0	sdk_version 26, 5
	.section	__TEXT,__text,regular,pure_instructions
	.globl	_add                            ; -- Begin function add
	.p2align	2
_add:                                   ; @add
	.cfi_startproc
; %bb.0:
	add	w0, w1, w0
	ret
	.cfi_endproc
                                        ; -- End function
	.globl	_sum                            ; -- Begin function sum
	.p2align	2
_sum:                                   ; @sum
	.cfi_startproc
; %bb.0:
	cmp	w1, #1
	b.lt	LBB1_4
; %bb.1:
	mov	w8, #0                          ; =0x0
	mov	w9, w1
LBB1_2:                                 ; =>This Inner Loop Header: Depth=1
	ldr	w10, [x0], #4
	add	w8, w10, w8
	subs	x9, x9, #1
	b.ne	LBB1_2
; %bb.3:
	mov	x0, x8
	ret
LBB1_4:
	mov	w8, #0                          ; =0x0
	mov	x0, x8
	ret
	.cfi_endproc
                                        ; -- End function
.subsections_via_symbols
