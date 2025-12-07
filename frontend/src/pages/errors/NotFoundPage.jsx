import { useNavigate } from "react-router-dom";
import "../../styles/components/common/FormControls.css";
import "../../styles/pages/errors/NotFoundPage.css";

export default function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="notfound">
      <h1>Error: 404</h1>
      <p>Nothing here</p>

      <button className="primary-btn" onClick={() => navigate("/")}>
        Back to Home
      </button>
    </div>
  );
}