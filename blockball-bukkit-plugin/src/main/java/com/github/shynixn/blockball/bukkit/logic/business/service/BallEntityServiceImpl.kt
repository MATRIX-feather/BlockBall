package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.BallSpawnEvent
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.core.logic.business.proxy.BallCrossPlatformProxy
import com.github.shynixn.blockball.core.logic.business.proxy.BallDesignEntity
import com.github.shynixn.blockball.core.logic.business.proxy.BallHitboxEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class BallEntityServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val proxyService: ProxyService,
    private val packetService: PacketService,
    private val concurrencyService: ConcurrencyService,
    private val itemTypeService: ItemTypeService,
    private val rayTracingService: RayTracingService,
    private val loggingService: LoggingService,
    private val eventService: EventService
) : BallEntityService, Runnable {

    private val balls = ArrayList<BallProxy>()

    init {
        plugin.server.scheduler.runTaskTimer(plugin, this, 0L, 20L * 60 * 5)
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            for (ball in balls) {
                ball.run()
            }
        }, 0L, 1L)
    }

    /**
     * Spawns a temporary ball.
     * Returns a ball or null if the ball spawn event was cancelled.
     */
    override fun <L> spawnTemporaryBall(location: L, meta: BallMeta): BallProxy? {
        require(location is Location)

        location.yaw = 0.0F

        val ballHitBoxEntity = BallHitboxEntity(
            proxyService.createNewEntityId()
        )
        ballHitBoxEntity.position = PositionEntity(location.world!!.name, location.x, location.y, location.z)
        ballHitBoxEntity.rayTracingService = rayTracingService
        ballHitBoxEntity.concurrencyService = concurrencyService
        ballHitBoxEntity.packetService = packetService
        ballHitBoxEntity.eventService = eventService
        ballHitBoxEntity.proxyService = proxyService

        val ballDesignEntity = BallDesignEntity(proxyService.createNewEntityId())
        ballDesignEntity.proxyService = proxyService
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemTypeService

        val ball = BallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball
        ball.loggingService = loggingService
        ball.eventService = eventService
        ball.proxyService = proxyService

        val event = BallSpawnEvent(ball)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return null
        }

        balls.add(ball)

        return ball
    }

    /**
     * Tries to locate the ball by the given id.
     */
    override fun findBallByEntityId(id: Int): BallProxy? {
        return balls.firstOrNull { b -> b.hitBoxEntityId == id || b.designEntityId == id }
    }

    /**
     * Disables a ball from tracking.
     */
    override fun removeTrackedBall(ball: BallProxy) {
        if (balls.contains(ball)) {
            balls.remove(ball)
        }
    }

    /**
     * Returns all balls managed by the plugin.
     */
    override fun getAllBalls(): List<BallProxy> {
        return balls
    }

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        balls.toTypedArray().forEach { ball ->
            if (ball.isDead) {
                this.balls.remove(ball)
            }
        }
    }
}
