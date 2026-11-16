#include "libaegis.h"

int main(void) {
  static const char banner[] = "[init] AegisOS-64 EL0 userland alive\n";
  sys_write(1, banner, sizeof(banner) - 1U);
  long pid = sys_getpid();
  sys_exit(pid > 0 ? 0 : 1);
  return 0;
}
