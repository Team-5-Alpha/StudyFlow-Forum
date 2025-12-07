import { useRef } from "react";
import api from "../../api/axios";
import { applyFollowState, cacheFollowState } from "../../utils/followUtils";

export function useFollowActions(targetUserId, options = {}) {
  const { setProfile, onChange, onError } = options;
  const inFlight = useRef(false);

  function updateProfileState(newState, updateFollowersCount) {
    if (setProfile) {
      setProfile(prev => {
        const current = prev?.followersCount ?? 0;
        const followersCount =
          typeof updateFollowersCount === "function"
            ? updateFollowersCount(current)
            : current;

        return {
          ...applyFollowState(prev || {}, newState),
          followersCount,
        };
      });
    }

    cacheFollowState(targetUserId, newState);

    if (onChange) {
      onChange(newState);
    }
  }

  async function follow() {
    if (inFlight.current) return;
    inFlight.current = true;

    try {
      await api.post(`/api/private/users/${targetUserId}/follow`);

      updateProfileState(true, c => c + 1);
    } catch (err) {
      console.error("FOLLOW ERROR:", err);

      const msg = String(err?.message || "").toLowerCase();
      if (msg.includes("already follow") || msg.includes("already following")) {
        updateProfileState(true);
      } else {
        onError?.(err);
      }
    } finally {
      inFlight.current = false;
    }
  }

  async function unfollow() {
    if (inFlight.current) return;
    inFlight.current = true;

    try {
      await api.delete(`/api/private/users/${targetUserId}/follow`);

      updateProfileState(false, c => Math.max(c - 1, 0));
    } catch (err) {
      console.error("UNFOLLOW ERROR:", err);

      const msg = String(err?.message || "").toLowerCase();
      if (msg.includes("not follow") || msg.includes("do not follow")) {
        updateProfileState(false);
      } else {
        onError?.(err);
      }
    } finally {
      inFlight.current = false;
    }
  }

  return { follow, unfollow };
}
