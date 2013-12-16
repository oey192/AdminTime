AdminTime Readme
====

AdminTime allows admins to switch in and out of an Admining state and allows them to resume where they left off when done admining. It can be insanely handy if your admins have a hard time restraining themselves from using admiring abilities during normal gameplay as it allows other admins to view who is currently using their AdminTime-granted admin abilities and notifies all admins (who have the appropriate permission) when players enter and leave Admin Mode.

It also helps admins admin more effectively by teleporting them back to the location they were at when they entered Admin Mode when they exit admin mode, allowing them to pick up right where they left off before going off to admin something. And, if enabled in the config file, admins gain both god mode\* and the ability to fly\* (don't worry, they aren't in creative, so they won't be spawning in items) when in Admin Mode

Players will not be informed when vanished players (VanishNoPacket) enter or exit Admin Mode, nor will vanished players show up in /admintime list.

Players are automatically removed from Admin Mode upon disconnecting if they forget to exit it first.

Note: If PermissionsEx is being used on your server, this plugin temporarily overrides permissions set by PermissionsEx when the appropriate config value is set to true. When this is set to false, this plugin only modifies permissions via the built-in Bukkit Pernussions API. This can cause certain permisisons used with this plugin to have no effect as some plugins (e.g. WorldEdit) talk directly to PermissionsEx to check what permissions a player has. It is therefore generally recommended to leave usePex set to true unless you don't use PermissionsEx

\*Requires Essentials

##Commands

Aliases: at, am, adminmode

/admintime [player] [reason]:<br/>Toggle admin mode. The name of the player being helped is required for entering admin mode. If that player is offline, their full name must be specified. A reason may be specified if desired.<br/>When leaving admin mode, the player is teleported to the locatation they were at when they entered admin mode

/admintime reload:<br/>Reload the AdminTime permissions file

/admintime version:<br/>Get the current version of AdminTime

/admintime list [#]:<br/>List players in admin mode, who they're helping and the reason if specified. Use page numbers if necessary. (Inputting page numbers higher than the last page will result in the last page being shown)

/admintime eject:<br/>Kick a player out of admin mode

/admintime help:<br/>Show in-game help


##Permissions

admintime.*:<br/>Gives access to all AdminTime commands

admintime.notify:<br/>Players with this permission are notified when other players enter and exit Admin mode

admintime.toggle:<br/>Allows players to toggle Admin Mode on and off to help themselves or other players

admintime.toggle.self:<br/>Allows players to toggle Admin Mode on and off to help themself

admintime.toggle.others:<br/>Allows players to toggle Admin Mode on and off to help other players.

admintime.reload:<br/>Allows players to reload the configuration and permissions files

admintime.version:<br/>Allows players to check the version

admintime.eject:<br/>Allows players to remove others from Admin Mode

admintime.list:<br/>Allows players to see who is in Admin Mode

admintime.loginlist:<br/>Shows a list of players in Admin Mode to the player upon logging in. This will show even if the player does not have admintime.list

##Config

useGod:<br/>If Essentials is present and this is set to true, god mode is turned on for the player upon entering Admin Mode and turned off when the player leaves Admin Mode

useFly:<br/>If Essentials is present and this is set to true, the player can access flight controls as if they were in creative mode when in Admin Mode

usePex:<br/>If PermissionsEx is present and this is set to true, permissions are set using PermissionsEx instead of with the built-in Bukkit API. Note that if the server crashes, any permissions set in PermissionsEx's permissions.yml file will be automatically fixed the next time any players in Admin Mode at the time of the crash log in

##permissions.yml

On plugin load, this file is populated with nodes for any new worlds that have been added since the last time the plugin was loaded. This includes the first time the plugin starts and as such should contain a full list of worlds after first launch. Each world is created with an empty permissions set (a set of open and close square braces: []). You may add permissions in the square brackets sepratated by commas, but as this quickly becomes difficult to read, it is recommended that all permissions should be put on their own line with a dash in front of them

The two overarching divisions, adminMode and regMode designate two separate permissions sets. One will be given to players in admin mode, the other for players not in admin mode (note: players must have permissions to get into admin mode before either set of permissions will be given). It should be rare to need to give admins any special permissions for when they're not in admin mode, but the functionality has been provided in case there's that one weird permission that disables a special ability and there's no permission that grants it

An example file follows that gives users access to essentials and prism when in admin mode in all worlds and access to world edit when in the test world. A fictional "can't build" permission is given to all admins in the test world

	adminMode:
		world: []
		testing:
		- worldedit.*
		allWorlds:
		- essentials.*
		- prism.*
	regMode:
		world: []
		testing:
		- worldguard.denybuild
		allWorlds: []

