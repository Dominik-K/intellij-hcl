# IntelliJ-HCL plugin

Provides [HCL language](https://github.com/hashicorp/hcl) and [Terraform](https://terraform.io) configuration files (.tf) support for [IntelliJ Platform](http://www.jetbrains.org/pages/viewpage.action?pageId=983889) based IDEs

### Features:
##### For both .hcl and .tf file formats:
* Syntax highlighting
* Structure outline in 'Structure' tool window
* Code formatter, so reformat code action available
* Code folding
* Comment/Uncomment action

#### Terraform configs (.tf) files
* Interpolations syntax highlighting

#### Terraform configs Interpolation Language
* Syntax highlighting



### Planned features:
#### Terraform configs (.tf) files
* Go to definition from resource to provider
* Find usages for resources, providers, variables
* Properties validation (according to required properties for resource/provider, type checking)

#### Terraform configs Interpolation Language
* [Predefined methods](https://www.terraform.io/docs/configuration/interpolation.html) autocompletion
* Go to declaration on resources, providers, properties, etc.