-- redis Lua脚本解决高并发在浏览量读取问题
local hashKey = KEYS[1]
local result = {}
local values = redis.call('HGETALL', hashKey)
for i = 1, #values, 2 do
    local field = values[i]
    local count = tonumber(values[i + 1])
    if count and count > 0 then
        table.insert(result, field)
        table.insert(result, count)
        -- 核心逻辑：原子性地把这个 字段 的值减去 count（相当于清零）
        -- 如果在 Java 同步 MySQL 的时候，又来了新的浏览量，它会在这个 0 的基础上继续加。
        redis.call('HINCRBY', hashKey, field, -count)
    end
end
return result