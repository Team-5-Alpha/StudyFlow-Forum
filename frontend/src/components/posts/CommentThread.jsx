import { useState } from "react";
import Spinner from "../common/Spinner";
import CommentItem from "./CommentItem";
import CommentForm from "./CommentForm";
import "../../styles/components/posts/CommentCard.css";
import "../../styles/components/common/FormControls.css";

export default function CommentThread({
  loading,
  comments,
  actions,
}) {
  const [newComment, setNewComment] = useState("");
  const [replyId, setReplyId] = useState(null);
  const [editingId, setEditingId] = useState(null);

  if (loading) return <Spinner />;

  return (
    <div className="comments">
      <h3>Comments</h3>

      {comments.length === 0 ? (
        <p>No comments yet.</p>
      ) : (
        comments.map((c) => (
          <CommentItem
            key={c.id}
            comment={c}
            level={0}
            replyId={replyId}
            setReplyId={setReplyId}
            editingId={editingId}
            setEditingId={setEditingId}
            actions={actions}
          />
        ))
      )}

      <div>
        <h4>Add Comment</h4>
        <CommentForm
          initialValue={newComment}
          submitLabel="Submit"
          rows={3}
          onSubmit={async (text) => {
            await actions.create(text);
            setNewComment("");
          }}
        />
      </div>
    </div>
  );
}
