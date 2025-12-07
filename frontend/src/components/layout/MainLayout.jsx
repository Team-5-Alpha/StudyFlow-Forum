import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";

import "../../styles/components/layout/MainLayout.css";

export default function MainLayout() {
  return (
    <div className="layout">
      <Navbar />
      
      <main className="content">
        <Outlet />
      </main>

      <Footer />
    </div>
  );
}