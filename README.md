AdminTime Readme
====

AdminTime allows admins to switch in and out of an Admining state and allows them to resume where they left off when done admining

Note: This plugin temporarily overrides permissions set by PermissionsEx when the appropriate config value is set to true. When this is set to false, this plugin only modifies permissions via the built-in Bukkit Pernussions API

<h2>Commands</h2>

Aliases: at, am, adminmode

/admintime [player]:<br/>Toggle admin mode. The name of the player being helped is required for entering admin mode. If that player is offline, their full name must be specified

/admintime reload:<br/>Reload the AdminTime permissions file

/admintime version:<br/>Get the current version of AdminTime

/admintime list [#]:<br/>List players in admin mode. Specify page numbers if necessary

/admintime eject:<br/>Kick a player out of admin mode

/admintime help:<br/>Show in-game help


<h2>Permissions</h2>

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

<h2>Config</h2>

useGod:<br/>If Essentials is present and this is set to true, god mode is turned on for the player upon entering Admin Mode and turned off when the player leaves Admin Mode

useFly:<br/>If Essentials is present and this is set to true, the player can access flight controls as if they were in creative mode when in Admin Mode

usePex:<br/>If PermissionsEx is present and this is set to true, permissions are set using PermissionsEx instead of with the built-in Bukkit API. Note that if the server crashes, any permissions set in PermissionsEx's permissions.yml file will be automatically fixed the next time any players in Admin Mode at the time of the crash log in