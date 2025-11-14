local key = KEYS[1]
local count = tonumber(ARGV[1])
local time = tonumber(ARGV[2])
local current = tonumber(redis.call("get", key) or "0")
if current > count then
    return current
end
current = tonumber(redis.call("incr", key))
if current == 1 then
    redis.call("expire", key, time)
end
return current