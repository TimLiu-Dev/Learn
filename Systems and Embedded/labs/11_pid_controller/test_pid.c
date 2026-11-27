#include <assert.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>

typedef struct {
    float kp;
    float ki;
    float kd;
    float integral;
    float prev_err;
    float out_max;
} PidState;

static float clampf_sym(float v, float limit) {
    if (v > limit) return limit;
    if (v < -limit) return -limit;
    return v;
}

static float pid_step(PidState *pid, float setpoint, float measured, float dt, bool overcurrent) {
    if (overcurrent) {
        pid->integral = 0.0f;
        pid->prev_err = 0.0f;
        return 0.0f;
    }
    float err = setpoint - measured;
    pid->integral = clampf_sym(pid->integral + pid->ki * err * dt, pid->out_max);
    float deriv = pid->kd * (err - pid->prev_err) / dt;
    pid->prev_err = err;
    return clampf_sym(pid->kp * err + pid->integral + deriv, pid->out_max);
}

int main(void) {
    PidState pid = { .kp = 2.0f, .ki = 10.0f, .kd = 0.05f, .integral = 0.0f, .prev_err = 0.0f, .out_max = 100.0f };
    float speed = 0.0f;
    const float setpoint = 60.0f;
    const float dt = 0.01f;

    for (int step = 0; step < 300; ++step) {
        float drive = pid_step(&pid, setpoint, speed, dt, false);
        assert(drive >= -100.0f && drive <= 100.0f);
        /* Simple first-order motor plant simulation */
        speed += (drive * 0.8f - speed) * dt * 8.0f;
    }
    assert(fabsf(speed - setpoint) < 2.5f);

    /* Simulate INA226 ALERT overcurrent trip -> immediate 0 output & integrator reset */
    float fault_drive = pid_step(&pid, setpoint, speed, dt, true);
    assert(fault_drive == 0.0f && pid.integral == 0.0f);

    printf("  [Lab 11] Anti-windup PID & overcurrent cutoff passed (final=%.2f RPM).\n", speed);
    return 0;
}
