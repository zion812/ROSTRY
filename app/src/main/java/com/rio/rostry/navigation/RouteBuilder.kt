package com.rio.rostry.navigation

/**
 * Utility object to build routes with required and optional arguments in a type-safe manner.
 */
object RouteBuilder {
    /**
     * Build a route with required arguments.
     * 
     * @param destination The destination to build the route for
     * @param requiredArgs A map of required argument names to their values
     * @return The complete route string with arguments substituted
     * @throws IllegalArgumentException if any required argument is missing
     */
    fun buildRouteWithRequiredArgs(
        destination: AppDestination,
        requiredArgs: Map<String, String>
    ): String {
        var route = destination.route
        
        // Check if all required arguments are provided
        destination.arguments.forEach { argument ->
            val argName = argument.name
            if (argName != null && !requiredArgs.containsKey(argName)) {
                throw IllegalArgumentException("Missing required argument: $argName for destination ${destination.route}")
            }
        }
        
        // Substitute provided arguments
        requiredArgs.forEach { (argName, argValue) ->
            route = route.replace("{$argName}", argValue)
        }
        
        return route
    }
    
    /**
     * Build a route with both required and optional arguments.
     * 
     * @param destination The destination to build the route for
     * @param requiredArgs A map of required argument names to their values
     * @param optionalArgs A map of optional argument names to their values
     * @return The complete route string with arguments substituted
     */
    fun buildRouteWithOptionalArgs(
        destination: AppDestination,
        requiredArgs: Map<String, String>,
        optionalArgs: Map<String, String> = emptyMap()
    ): String {
        var route = buildRouteWithRequiredArgs(destination, requiredArgs)
        
        // Substitute optional arguments
        optionalArgs.forEach { (argName, argValue) ->
            route = route.replace("{$argName}", argValue)
        }
        
        return route
    }
}