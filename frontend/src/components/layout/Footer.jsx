import "../../styles/components/layout/Footer.css";

export default function Footer() {
  return (
    <footer className="footer-container">
      StudyFlow © {new Date().getFullYear()}
    </footer>
  );
}