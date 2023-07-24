package org.scuvis.community.service;

import org.scuvis.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Xiyao Li
 * @date 2023/07/24 23:24
 */
@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    // sdf的作用：把Date对象转化为特定格式的字符串
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public void recordUV(String ip){
        String uvKey = RedisUtil.getUVKey(sdf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(uvKey,ip);
    }

    public Long calculateUV(Date startDate, Date endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("参数为空！");
        }

        // 把这些日期里的key放在keyList里
        List<String> keyList = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while(!calendar.getTime().after(endDate)){
            String uvKey = RedisUtil.getUVKey(sdf.format(calendar.getTime()));
            keyList.add(uvKey);
            calendar.add(Calendar.DATE,1);
        }

        // 把keyList里的key合并到一个新key里
        String newUVKey = RedisUtil.getUVKey(sdf.format(startDate), sdf.format(endDate));
        redisTemplate.opsForHyperLogLog().union(newUVKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(newUVKey);
    }

    public void recordDAU(int userId){
        String dauKey = RedisUtil.getDAUKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey,userId,true);
    }

    public Long calculateDAU(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数为空！");
        }

        // 把这些日期里的key放在keyList里
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            String dauKey = RedisUtil.getDAUKey(sdf.format(calendar.getTime()));
            keyList.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisUtil.getDAUKey(sdf.format(startDate), sdf.format(endDate));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
