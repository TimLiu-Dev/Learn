#include "aegis/kernel.h"

static bool str_eq(const char *a, const char *b) {
  while (*a && *b) {
    if (*a != *b) return false;
    a++;
    b++;
  }
  return *a == *b;
}

static void str_copy_lim(char *dst, const char *src, size_t max_len) {
  size_t i = 0;
  while (i + 1 < max_len && src[i] != '\0') {
    dst[i] = src[i];
    i++;
  }
  dst[i] = '\0';
}

void vfs_init(struct vfs_fs *fs) {
  for (int i = 0; i < VFS_MAX_INODES; i++) {
    fs->inodes[i].type = INODE_FREE;
    fs->inodes[i].parent_id = -1;
    fs->inodes[i].size = 0;
    fs->inodes[i].name[0] = '\0';
  }
  fs->inodes[0].type = INODE_DIR;
  fs->inodes[0].parent_id = 0;
  str_copy_lim(fs->inodes[0].name, "/", VFS_MAX_NAME);
}

static int find_child(const struct vfs_fs *fs, int parent_id, const char *seg) {
  for (int i = 0; i < VFS_MAX_INODES; i++) {
    if (fs->inodes[i].type != INODE_FREE &&
        fs->inodes[i].parent_id == parent_id &&
        str_eq(fs->inodes[i].name, seg)) {
      return i;
    }
  }
  return -ENOENT;
}

int vfs_lookup(const struct vfs_fs *fs, const char *path) {
  if (!fs || !path || path[0] != '/') return -EINVAL;
  int cur = 0;
  size_t i = 1;
  while (path[i] != '\0') {
    while (path[i] == '/') i++;
    if (path[i] == '\0') break;
    if (fs->inodes[cur].type != INODE_DIR) return -ENOTDIR;

    char seg[VFS_MAX_NAME];
    size_t k = 0;
    while (path[i] != '\0' && path[i] != '/') {
      if (k + 1 < VFS_MAX_NAME) seg[k++] = path[i];
      i++;
    }
    seg[k] = '\0';
    cur = find_child(fs, cur, seg);
    if (cur < 0) return cur;
  }
  return cur;
}

int vfs_create(struct vfs_fs *fs, const char *path, uint8_t type) {
  if (!fs || !path || path[0] != '/' || (type != INODE_FILE && type != INODE_DIR)) {
    return -EINVAL;
  }
  /* Split into parent path and leaf name */
  int last_slash = 0;
  for (int i = 0; path[i] != '\0'; i++) {
    if (path[i] == '/') last_slash = i;
  }
  const char *leaf = path + last_slash + 1;
  if (leaf[0] == '\0') return -EINVAL;

  int parent = 0;
  if (last_slash > 0) {
    char pbuf[128];
    int n = last_slash < 127 ? last_slash : 127;
    for (int i = 0; i < n; i++) pbuf[i] = path[i];
    pbuf[n] = '\0';
    parent = vfs_lookup(fs, pbuf);
    if (parent < 0) return parent;
  }
  if (fs->inodes[parent].type != INODE_DIR) return -ENOTDIR;
  if (find_child(fs, parent, leaf) >= 0) return -EEXIST;

  for (int i = 1; i < VFS_MAX_INODES; i++) {
    if (fs->inodes[i].type == INODE_FREE) {
      fs->inodes[i].type = type;
      fs->inodes[i].parent_id = parent;
      fs->inodes[i].size = 0;
      str_copy_lim(fs->inodes[i].name, leaf, VFS_MAX_NAME);
      return i;
    }
  }
  return -ENOSPC;
}

ssize_t vfs_write(struct vfs_fs *fs, int ino, size_t offset, const void *buf, size_t len) {
  if (!fs || ino < 0 || ino >= VFS_MAX_INODES) return -EBADF;
  struct vfs_inode *node = &fs->inodes[ino];
  if (node->type != INODE_FILE) return -EINVAL;
  if (offset >= VFS_FILE_CAP) return -ENOSPC;
  size_t avail = VFS_FILE_CAP - offset;
  size_t n = len < avail ? len : avail;
  const uint8_t *src = (const uint8_t *)buf;
  for (size_t i = 0; i < n; i++) {
    node->data[offset + i] = src[i];
  }
  if (offset + n > node->size) node->size = offset + n;
  return (ssize_t)n;
}

ssize_t vfs_read(const struct vfs_fs *fs, int ino, size_t offset, void *buf, size_t len) {
  if (!fs || ino < 0 || ino >= VFS_MAX_INODES) return -EBADF;
  const struct vfs_inode *node = &fs->inodes[ino];
  if (node->type != INODE_FILE) return -EINVAL;
  if (offset >= node->size) return 0;
  size_t avail = node->size - offset;
  size_t n = len < avail ? len : avail;
  uint8_t *dst = (uint8_t *)buf;
  for (size_t i = 0; i < n; i++) {
    dst[i] = node->data[offset + i];
  }
  return (ssize_t)n;
}
