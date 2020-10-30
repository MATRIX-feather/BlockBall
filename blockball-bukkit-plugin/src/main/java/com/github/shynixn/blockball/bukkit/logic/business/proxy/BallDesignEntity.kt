package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.ItemTypeService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.core.logic.persistence.entity.EntityMetadataImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.ItemEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class BallDesignEntity(val entityId: Int) {
    /**
     * Rotation of the design in euler angles.
     */
    var rotation: Position = PositionEntity(0.0, 0.0, 0.0)

    /**
     * Requests a change of rotations.
     */
    var requestRotationChange: Boolean = false

    /**
     * Plays animation backwards or forwards.
     */
    var backAnimation = false

    /**
     * Proxy service dependency.
     */
    lateinit var proxyService: ProxyService

    /**
     * Packet service dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Item service dependency.
     */
    lateinit var itemService: ItemTypeService

    /**
     * Reference.
     */
    lateinit var ball: BallProxy

    private val helmetItemStack by lazy {
        val item = ItemEntity {
            this.type = MaterialType.SKULL_ITEM.MinecraftNumericId.toString()
            this.dataValue = 3
            this.skin = ball.meta.skin
        }

        itemService.toItemStack<ItemStack>(item)
    }

    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun <P> tick(players: List<P>) {
        val position = ball.getLocation<Location>().toPosition()

        for (player in players) {
            packetService.sendEntityTeleportPacket(player, entityId, position)
        }

        if (ball.meta.rotating) {
            playRotationAnimation()
        }

        if (requestRotationChange) {
            for (player in players) {
                packetService.sendEntityMetaDataPacket(player, entityId, EntityMetadataImpl {
                    this.armorstandHeadRotation = rotation
                })
            }

            requestRotationChange = false
        }
    }

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Player, position: Position) {
        packetService.sendEntitySpawnPacket(player, entityId, "ARMOR_STAND", position)
        packetService.sendEntityEquipmentPacket(player, entityId, 5, helmetItemStack)
    }

    /**
     * Destroys the ball for the given player.
     */
    fun destroy(player: Player) {
        packetService.sendEntityDestroyPacket(player, entityId)
    }

    /**
     * Plays the rotation animation.
     */
    private fun playRotationAnimation() {
        val length = ball.getVelocity<Vector>().length()

        val a = rotation
        val angle = when {
            length > 1.0 -> if (this.backAnimation) {
                PositionEntity(a.x - 0.5, 0.0, 0.0)
            } else {
                PositionEntity(a.x + 0.5, 0.0, 0.0)
            }
            length > 0.1 -> if (this.backAnimation) {
                PositionEntity(a.x - 0.25, 0.0, 0.0)
            } else {
                PositionEntity(a.x + 0.25, 0.0, 0.0)
            }
            length > 0.08 -> if (this.backAnimation) {
                PositionEntity(a.x - 0.025, 0.0, 0.0)
            } else {
                PositionEntity(a.x + 0.025, 0.0, 0.0)
            }
            else -> null
        }

        if (angle != null) {
            rotation = angle
            requestRotationChange = true
        }
    }
}
