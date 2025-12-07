export function isEmail(value) {
  return /\S+@\S+\.\S+/.test(value);
}

export function minLength(value, len) {
  return value.trim().length >= len;
}

export function required(value) {
  return value.trim().length > 0;
}

export function validateForm(fields, rules) {
  const errors = {};

  for (const key in rules) {
    for (const rule of rules[key]) {
      const message = rule(fields[key]);
      if (message) {
        errors[key] = message;
        break;
      }
    }
  }

  return errors;
}