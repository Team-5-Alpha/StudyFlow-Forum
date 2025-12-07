import { normalizeUserSummary } from "./followUtils";

const likeStateCache = new Map();

export function cacheLikeState(postId, liked, likesCount) {
  if (postId === undefined || postId === null) return;
  const key = String(postId);
  const entry = likeStateCache.get(key) || {};
  likeStateCache.set(key, {
    liked: liked ?? entry.liked,
    likesCount: likesCount ?? entry.likesCount,
  });
}

export function getCachedLikeState(postId) {
  if (postId === undefined || postId === null) return {};
  const entry = likeStateCache.get(String(postId));
  return entry || {};
}

export function resolveIsLiked(post = {}, prevPost = {}) {
  const cached = getCachedLikeState(post.id);
  return Boolean(
    post.likedByCurrentUser ??
      post.isLikedByCurrentUser ??
      post.liked ??
      post.likedByMe ??
      prevPost?.likedByCurrentUser ??
      prevPost?.isLikedByCurrentUser ??
      prevPost?.liked ??
      prevPost?.likedByMe ??
      cached.liked ??
      false
  );
}

export function resolveLikesCount(post = {}, prevPost = {}) {
  const cached = getCachedLikeState(post.id);
  const value =
    post.likesCount ??
    post.likeCount ??
    prevPost?.likesCount ??
    prevPost?.likeCount ??
    cached.likesCount ??
    0;
  return Number.isFinite(value) ? value : 0;
}

export function applyLikeState(entity, liked, likesCount) {
  const base = entity ?? {};
  const nextLikes =
    likesCount !== undefined
      ? likesCount
      : Math.max((base.likesCount ?? 0) + (liked ? 1 : -1), 0);

  cacheLikeState(base.id, liked, nextLikes);

  return {
    ...base,
    likedByCurrentUser: liked,
    isLikedByCurrentUser: liked,
    likedByMe: liked,
    likesCount: nextLikes,
  };
}

export function normalizePost(post = {}, prevPost = {}) {
  const liked = resolveIsLiked(post, prevPost);
  const likesCount = resolveLikesCount(post, prevPost);

  return {
    ...applyLikeState(post, liked, likesCount),
    author: post.author ? normalizeUserSummary(post.author) : post.author,
  };
}
