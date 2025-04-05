$JARS_FOLDER = "jars"  # Folder containing your JARs
$GROUP_ID = "com.example"  # Base groupId for all JARs

Get-ChildItem "$JARSFOLDER*.jar" | ForEach-Object {
    $filename = $.Name
    $artifactId = $filename -replace '-[\d.]+.jar$', ''  # Extract artifactId (e.g., "besu-datatypes")
    $version = $filename -replace '.*-([\d.]+).jar$', '$1'  # Extract version (e.g., "4.0.4")

    # Install to local Maven repo
    mvn install:install-file
        -Dfile=$_.FullName
        -DgroupId=$GROUP_ID
        -DartifactId=$artifactId
        -Dversion=$version `
        -Dpackaging=jar
}

Write-Host "All JARs installed to local Maven repo (~/.m2)." -ForegroundColor Green