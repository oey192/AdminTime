AdminTime Readme
====

AdminTime allows admins to switch in and out of an Admining state and allows them to resume where they left off when done admining

Note: This plugin does not override permissions set by other permission plugins. If you are using WorldEdit and PermissionsEx and set the appropriate config value to true, this plugin will override those permissions (if a player normally has access to a permission that people have access to in Admin Mode, they will have to be in Admin Mode to use it).

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

admintime.toggle:<br/>Allows players to toggle Admin Mode on and off

admintime.reload:<br/>Allows players to reload the configuration and permissions files

admintime.version:<br/>Allows players to check the version

admintime.eject:<br/>Allows players to remove others from Admin Mode

admintime.list:<br/>Allows players to see who is in Admin Mode

<h2>Config</h2>

useGod:<br/>If Essentials is present and this is set to true, god mode is turned on for the player upon entering Admin Mode and turned off when the player leaves Admin Mode

usePexForWE:<br/>If PermissionsEx is present and this is set to true, PermissionsEx is used for WorldEdit permissions. Since WorldEdit checks directly with PermissionsEx if permissions are set or not, this is required if you want to give players access to WorldEdit commands while in Admin Mode if you use PermissionsEx for permissions. The downside to this is that if the server crashes while a player is in Admin Mode, PermissionsEx will save the Admin Mode permissions for that player and they will have those permissions all the time.