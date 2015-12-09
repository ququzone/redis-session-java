package edu.nwnu.ququzone.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis session repository.
 *
 * @author Yang XuePing
 */
public class RedisSessionRepository implements SessionRepository {
    private static Logger LOG = LoggerFactory.getLogger(RedisSessionRepository.class);

    private int timeout = (int) TimeUnit.HOURS.toSeconds(24);

    private String prefix = "sessions:";

    private JedisPool pool;

    public RedisSessionRepository(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public void save(String id, Map<String, String> session) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hmset(prefix + id, session);
            jedis.expire(prefix + id, timeout);
        } catch (Exception e) {
            LOG.error("save session to redis exception.", e);
        }
    }

    @Override
    public Map<String, String> get(String id) {
        try (Jedis jedis = pool.getResource()) {
            Map<String, String> data = jedis.hgetAll(prefix + id);
            jedis.expire(prefix + id, timeout);
            return data;
        } catch (Exception e) {
            LOG.error("get session from redis exception.", e);
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(prefix + id);
        } catch (Exception e) {
            LOG.error("delete session from redis exception.", e);
        }
    }
}