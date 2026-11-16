#ifndef AEGIS_TYPES_H
#define AEGIS_TYPES_H

#ifdef AEGIS_HOST_TEST
#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>
#include <sys/types.h>
#else
typedef unsigned char      uint8_t;
typedef unsigned short     uint16_t;
typedef unsigned int       uint32_t;
typedef unsigned long long uint64_t;
typedef signed char        int8_t;
typedef short              int16_t;
typedef int                int32_t;
typedef long long          int64_t;
typedef uint64_t           size_t;
typedef int64_t            ssize_t;
typedef uint64_t           uintptr_t;
typedef _Bool              bool;
#define true  1
#define false 0
#define NULL  ((void *)0)
#endif

#define AEGIS_PAGE_SIZE    4096ULL
#define AEGIS_USER_LIMIT   0x0000800000000000ULL

/* POSIX-compatible negative errno values */
#define EPERM   1
#define ENOENT  2
#define E2BIG   7
#define EBADF   9
#define ENOMEM  12
#define EACCES  13
#define EFAULT  14
#define EEXIST  17
#define ENOTDIR 20
#define EINVAL  22
#define ENOSPC  28
#define ENOSYS  38
#define ELOOP   40

#endif /* AEGIS_TYPES_H */
