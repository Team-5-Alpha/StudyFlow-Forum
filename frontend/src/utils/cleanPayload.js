export function cleanPayload(obj) {
    const cleaned = {};

    for (const key in obj) {
        let value = obj[key];

        if (typeof value === "string") {
            value = value.trim();
        }

        if (value === "" || value === null || value === undefined) {
            continue;
        }

        cleaned[key] = value;
    }

    return cleaned;
}