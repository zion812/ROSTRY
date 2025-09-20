# Check for processes that have handles to files in the build directory
# Using PowerShell's Get-FileLockInfo if available (requires PowerShell 5.1+)

# Function to get file lock information
function Get-FileLockInfo {
    param(
        [string]$Path
    )
    
    if (-not (Test-Path $Path)) {
        Write-Host "Path not found: $Path"
        return
    }
    
    # Try using handle.exe from Sysinternals if available
    if (Get-Command "handle.exe" -ErrorAction SilentlyContinue) {
        Write-Host "Using handle.exe to check for file locks:"
        & handle.exe $Path
    } else {
        # Fallback method using WMI
        Write-Host "Checking for processes using files in: $Path"
        
        # Get all processes
        $processes = Get-Process
        
        foreach ($process in $processes) {
            try {
                # Check if process has modules loaded from our path
                $modules = $process.Modules | Where-Object { $_.FileName -like "$Path*" }
                if ($modules) {
                    Write-Host "Process: $($process.ProcessName) (PID: $($process.Id))"
                    $modules | ForEach-Object {
                        Write-Host "  Module: $($_.FileName)"
                    }
                }
                
                # Check if process's working directory is in our path
                $workingDir = (Get-WmiObject -Class Win32_Process -Filter "ProcessId=$($process.Id)").WorkingDirectory
                if ($workingDir -like "$Path*") {
                    Write-Host "Process: $($process.ProcessName) (PID: $($process.Id)) has working directory: $workingDir"
                }
            } catch {
                # Ignore access denied errors
            }
        }
    }
}

# Run the function on our build directory
Get-FileLockInfo -Path "C:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\build"