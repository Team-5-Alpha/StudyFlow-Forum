export default function FieldLengthHint({ value = "", min = 0, max }) {
  const length = (value || "").length;
  const invalid = length < min || (max && length > max);

  return (
    <span className={`length-hint ${invalid ? "invalid" : ""}`}>
      {length}/{max || "∞"} {min ? `(min ${min})` : ""}
    </span>
  );
}
