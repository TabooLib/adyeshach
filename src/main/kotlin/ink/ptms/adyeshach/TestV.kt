package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.type.impl.AdyHuman
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-v", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    val entity = AdyHuman(sender)

                    entity.spawn(sender.location)
                    entity.setSkinData(
                            cape = true,
                            jacket = true,
                            leftSleeve = true,
                            rightSleeve = true,
                            leftPants = true,
                            rightPants = true,
                            hat = true,
                            unused = true
                    )

                    Tasks.delay(20, true) {
                        entity.setTexture(
                                "ewogICJ0aW1lc3RhbXAiIDogMTU5NjU1NDUzOTUxNywKICAicHJvZmlsZUlkIiA6ICJhMzZkNzY0NDhkYWU0NmIxYmQwMDU0MjJjNjYyOTk3ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCYWt1cml0X01hdWVpYyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NzRhYzQyNDM1ZTJmMmU4NjA3ZTAxZTAyMmJmOGIzNWMzZDNhNDI0ZDQxMmEzMmQ5YTRmNWU1NzRiZDU2NzZjIgogICAgfQogIH0KfQ==",
                                "LsTlirikbmJnrjhlIE4A7WLBSuVl3osjv2Gs1ewsQyLvQUlxDfqb6HD00+2bCJzCcxPefiDMJK8To0/0mPh+3fhxW199qhAMM/mUJZV6LjTilroPN6m3cFzzjBysAAl04cmewRJuZfc1ZalkrtUNAfxUW6kBzXsg6ZXDlI54OCj6Aygoz0uNpr19uqsAt4auv0cClPaCsmWob5+DHQZk6fxWkhs+TgvGCcBlTDS6tF/X5juJdSJ2qwvJPo3DLYq4HKV/0AOaUbCN4le8zZHoxjtX7HcYPDe9suUtzy8F+1/fnEPzMOjXPyKK3vlUmRnhnAQVQnM54uA/g/0xa7zCNpBqhb5HYHJMVk/LyN1DVq70r+wU3P8ycDYpWegrnc+QnDJuntntFsbWo88JJSgL3LzTEvXyT9H1hvErzCyS8T9cxgbD6FwqJlMiQARbx0wFGFQaDW4ycw7mD5Fkd+s8mEYIntrS7BFj12cgZOroORxz6O43N3G4or60ZHgVI3GgB/WIZL1tNNreXnsB6krrOBITgR+p8HKIZrLo2oSnZHqR3sElZ52Qnm++jOBkJh5DGCPmhUlsknCawTaqHLuk+s47pz4T1+pb2cnAaN73aPOK+TV5XSmJ1LEiI1leHI7BLYdMKvSWwmeeJQGhlb/RplmmcUJTpI3PgBlzHwMrNTI="
                        )
                        entity.setName("BKM")
                    }

                    Tasks.delay(60, true) {
                        entity.setTexture(
                                "ewogICJ0aW1lc3RhbXAiIDogMTU5NjU1NDU0NDg5NSwKICAicHJvZmlsZUlkIiA6ICJlMWRhZmE4MzIwNDc0YjI4OGUyYzA1ZGExYTg0M2NmNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmFzcGxlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzMGE1ODkwNTViMTkyMzk3NzY1MmZmYmI4OTI3Y2Y0MDRjNTFlOTJiYzQxM2VhNjJhYzNkMjdmYzBkNDYwMjEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                                "xKP6EOJvq6lakCa4k4InsYzqcASgydk9OL+v+bD/iWdXajQbnflcfs0WZjIWGrGXklsH05BLM9bc6EkfquEDyyrqjvdQtectdxTA63IPWzwbkEoSZA56q7AXg2CnuFjs6MEzo3WQkThPhsFn/+mxvQaRpW8XDCt4jaK96LryUWIuJjqJGNN3w6FGTdkSVuYcEKmb8Xqr1MCubuANG92+bOgfMzOMGpO7jYi+GW0rElVmgjI6MwsQrkrA2xJ8MxWzlVTjwLcHaF8HixTzS6zWrdcQeJmhQQ4/IKCaWV7RNWgcMAjOw9Qq4g2pirf5ZusFnbXCl0tY96Q8dW/NjSoLR+kENNF6M40ZhoNWn5Wpn7539D680AG9pxhanfEkZ8UsuROJw3pIELW9Cog4jH5UF+ELsZpy2j9yUnIjRKdPoqNhhV+gGlxQ0PuagY/RixkCJ1RKfHAsVOAcqQMwDxHWXOGkW+tx+o6TsHbpz1viE7Fbslz+X238/jUxfx0bDnjwWCOh7yBz7VyJWMYy02VMsc2zK7oH1Igp9Q1ik7GCcP4pylDYKlGUHV1/4dJAqK4iTunq486LwaNg8D0P06nmL2UmYE0jmufmDOdJNhBsCgJ7+/aO0I0XSaxK3mRfyZZdw05iOfHwjaMX+kLTLDsGEz+6pIZmSo7S1Lwy67g/SAs="
                        )
                        entity.setName("AAA")
                    }

                    Tasks.delay(300, true) {
                        entity.destroy()
                        sender.sendMessage("§c[System] §7Removed.")
                    }

                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}