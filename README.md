# GWT MD Icons for Domino UI (https://github.com/DominoKit/domino-ui) #

This is a Java annotation processor to generate Domino UI MdiIcons (https://dominokit.github.io/domino-ui-demo/index.html?theme=indigo#mdiicons and https://materialdesignicons.com/) with metainformation. 

* Allows easy update icon set in the application/Domino UI 
* Ptovide icon metainformation: tags, aliases, icon version and author.
* Provide single collection with all icons 
* Allows to select icon(s) by tag or alias
* Provide complete list of tags for current icon set.

### How to Use ###

1. Add annotation processor to your build
1. Add annotation MdIcon to a package
1. Put meta.json (https://github.com/Templarian/MaterialDesign-SVG) to the root of project sources 
1. Build it

The MdIcons interface will be created in the annotated package. The interface contains all icons from meta.json separated by alfabet (due to Java limitation on class initialization size)

