const followStateCache = new Map();

export function cacheFollowState(userId, isFollowed) {
  if (typeof userId === "number" || typeof userId === "string") {
    followStateCache.set(String(userId), Boolean(isFollowed));
  }
}

export function getCachedFollowState(userId) {
  if (typeof userId === "number" || typeof userId === "string") {
    const key = String(userId);
    return followStateCache.has(key) ? followStateCache.get(key) : undefined;
  }
  return undefined;
}

export function resolveIsFollowed(user = {}) {
  return Boolean(
    user.followedByCurrentUser ??
      user.isFollowedByCurrentUser ??
      user.followedByMe ??
      user.isFollowedByMe ??
      getCachedFollowState(user.id) ??
      false
  );
}

export function applyFollowState(entity, isFollowed) {
  const base = entity ?? {};

  cacheFollowState(base.id, isFollowed);

  return {
    ...base,
    followedByCurrentUser: isFollowed,
    isFollowedByCurrentUser: isFollowed,
    followedByMe: isFollowed,
    isFollowedByMe: isFollowed,
  };
}

export function normalizeUserSummary(user = {}) {
  const cached = getCachedFollowState(user.id);
  const isFollowed = cached !== undefined ? cached : resolveIsFollowed(user);

  return {
    ...applyFollowState(user, isFollowed),
    followersCount:
      user.followersCount ??
      user.followers ??
      user.followersTotal ??
      0,
    followingCount:
      user.followingCount ??
      user.following ??
      user.followingTotal ??
      0,
  };
}
