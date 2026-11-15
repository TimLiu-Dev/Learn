"use strict";

function createLogger(serviceName) {
  function emit(level, msg, fields) {
    const entry = Object.assign(
      {
        ts: new Date().toISOString(),
        level,
        service: serviceName,
        msg
      },
      fields || {}
    );
    const line = JSON.stringify(entry);
    if (level === "error") {
      process.stderr.write(line + "\n");
    } else {
      process.stdout.write(line + "\n");
    }
  }

  return {
    info: (msg, fields) => emit("info", msg, fields),
    warn: (msg, fields) => emit("warn", msg, fields),
    error: (msg, fields) => emit("error", msg, fields)
  };
}

module.exports = { createLogger };
